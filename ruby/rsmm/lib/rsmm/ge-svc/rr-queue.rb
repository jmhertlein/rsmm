class RoundRobinQueue
  def initialize
    @order = []
    @map = {}
    @robin_index = 0
  end

  def enqueue key, value
    if !@order.include? key
      @order << key
      @map[key] = []
    end

    @map[key] << value
  end

  def empty?
    return @order.empty?
  end

  def pop
    if @order[@robin_index].nil?
      if @robin_index == 0
        return nil
      else
        @robin_index = 0
        return pop
      end
    end

    queue_for_key = @map[@order[@robin_index]]
    ret = queue_for_key.shift
    if queue_for_key.empty?
      @map.delete @order.delete_at @robin_index
    else
      @robin_index += 1
    end
      
    return ret
  end
end
