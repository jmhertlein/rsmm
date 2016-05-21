class Table
  attr_reader :headers, :rows

  def initialize headers
    @headers = headers
    @rows = Array.new
    @rows << Array.new
  end
  
  def << value
    @rows << Array.new if @rows[-1].size == @headers.size
    @rows[-1] << value
  end

  def [] row_no
    return @rows[row_no]
  end

  def to_html
    out = "<table><tr>#{@headers.map{|h| "<th>#{h}</th>"}.join}</tr>"
    out += @rows.map{|r| "<tr>#{r.map{|td| "<td>#{td}</td>"}.join}</tr>"}.join
    return out
  end
end
