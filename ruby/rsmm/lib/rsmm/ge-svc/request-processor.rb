require 'rsmm/ge-svc/rr-queue'
require 'rsmm/jamflex'
class RequestProcessor
  BURST_THROTTLE_INTERVAL_SECONDS = 60 * 60
  SAFE_DELAY_INTERVAL_SECONDS = 5
  def initialize
    @last_throttle_time = Time.at(0)
    @last_req_time = Time.at(0)
    @queue = RoundRobinQueue.new
    @queue_mutex = Mutex.new
    @shutdown = false
  end

  def shutdown
    @shutdown = true
  end
  
  def submit request
    @queue_mutex.synchronize {
      @queue.enqueue request.name, request
    }
  end

  def start
    Thread.start() do
      while !@shutdown do
        process_queue
        sleep 0.5
      end
    end
  end

  def process_queue
    while @queue_mutex.synchronize { !@queue.empty? } do
      request = @queue_mutex.synchronize { @queue.pop }
      puts "Handling request for #{request.name}"
      wait_for_api_throttle
      str = Jamflex::get_json(request.url)
      mark_request_time
      while str.nil? do
        mark_throttle_time
        wait_for_api_throttle
        str = Jamflex::get_json(request.url)
        mark_request_time
      end

      begin
        request.handle_results(str)
      rescue Errno::EPIPE => err
        puts "Error writing response for #{request.name}: #{err}"
      end
    end
  end

  def wait_for_api_throttle
    if (Time.now - @last_throttle_time) > BURST_THROTTLE_INTERVAL_SECONDS
      return
    end

    if (Time.now - @last_req_time) > SAFE_DELAY_INTERVAL_SECONDS
      return
    end
    sleep_for = SAFE_DELAY_INTERVAL_SECONDS - (Time.now - @last_req_time)
    sleep(sleep_for + 0.1)
  end

  def mark_request_time
    @last_req_time = Time.now
  end

  def mark_throttle_time
    puts "Throttling detected."
    @last_throttle_time = Time.now
  end

  private :process_queue, :wait_for_api_throttle, :mark_request_time, :mark_throttle_time
end
