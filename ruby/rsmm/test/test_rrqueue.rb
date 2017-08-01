require 'minitest/autorun'
require 'rsmm/ge-svc/rr-queue.rb'

class TestMeme < MiniTest::Test
  def test_bulk_insert
    q = RoundRobinQueue.new
    q.enqueue "a", 1
    q.enqueue "b", 2
    q.enqueue "b", 3
    q.enqueue "b", 4
    q.enqueue "b", 5
    q.enqueue "c", 6


    assert_equal 1, q.pop
    assert_equal 2, q.pop
    assert_equal 6, q.pop
    assert_equal 3, q.pop
    assert_equal 4, q.pop
    assert_equal 5, q.pop
  end

  def test_mixed_insert_and_pop
    q = RoundRobinQueue.new
    q.enqueue "a", 1
    assert_equal 1, q.pop
    q.enqueue "a", 1
    assert_equal 1, q.pop

    q.enqueue "b", 1
    assert_equal 1, q.pop
  end

  def test_empty_pop
    q = RoundRobinQueue.new
    assert_equal nil, q.pop

    q.enqueue "a", 1
    assert_equal 1, q.pop
    assert_equal nil, q.pop
  end
end
