class Summary
  attr_reader :name, :id, :action, :reason, :price

  def initialize name, id, action, reason, price
    @name = name
    @id = id
    @action = action
    @reason = reason
    @price = price
  end

  def [] field
    return send(field)
  end
end
