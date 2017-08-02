class ItemTracker
  def initialize conn, rs_type
    @conn = conn
    @rs_type = rs_type
  end

  def name_for id
    res = @conn.exec_params('SELECT item_name FROM Item WHERE item_id=$1 and rs_type=$2', [id, @rs_type.to_s])
    return res.ntuples == 0 ? nil : res[0]["item_name"]
  end

  def id_for item_name
    res = @conn.exec_params('SELECT item_id FROM Item WHERE item_name=$1 and rs_type=$2', [item_name, @rs_type.to_s])
    return res.ntuples != 1 ? nil : res[0]["item_id"]
  end

  def limit_for id
    res = @conn.exec_params('SELECT ge_limit FROM Item WHERE item_id=$1 and rs_type=$2', [id, @rs_type.to_s])
    ret = res.ntuples == 0 ? nil : res[0]["ge_limit"]
    return ret.nil? ? ret : ret.to_i
  end

  def record_id itemid, name
    puts "Updating #{itemid} => #{name}"
    res = @conn.exec_params('UPDATE Item SET item_name=$1 WHERE item_id=$2 and rs_type=$3', [name, itemid, @rs_type])
    if res.cmd_tuples == 0
      puts "Couldn't update, inserting..."
      @conn.exec_params('INSERT INTO Item(item_id, item_name, rs_type) VALUES($1, $2, $3)', [itemid, name, @rs_type])
    end
  end

  def record_limit itemid, limit
    res = @conn.exec_params('UPDATE Item SET ge_limit=$1 WHERE item_id=$2 and rs_type=$3', [limit, itemid, @rs_type])
    if res.cmd_tuples == 0
      puts "Couldn't insert limit for #{itemid}"
    end
  end

  def count_all
    return @conn.exec_params('SELECT COUNT(*) AS count FROM Item WHERE rs_type=$1', [@rs_type])[0]["count"]
  end
end
