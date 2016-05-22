class ItemTracker
  def initialize conn
    @conn = conn
  end

  def name_for id
    res = @conn.exec_params('SELECT item_name FROM Item WHERE item_id=$1', [id])
    return res.ntuples == 0 ? nil : res[0]["item_name"]
  end

  def id_for item_name
    res = @conn.exec_params('SELECT item_id FROM Item WHERE item_name=$1', [item_name])
    return res.ntuples != 1 ? nil : res[0]["item_id"]
  end

  def limit_for id
    res = @conn.exec_params('SELECT ge_limit FROM Item WHERE item_id=$1', [id])
    ret = res.ntuples == 0 ? nil : res[0]["ge_limit"]
    return ret.nil? ? ret : ret.to_i
  end

  def record_id itemid, name
    puts "Updating #{itemid} => #{name}"
    res = @conn.exec_params('UPDATE Item SET item_name=$1 WHERE item_id=$2', [name, itemid])
    if res.cmd_tuples == 0
      puts "Couldn't update, inserting..."
      @conn.exec_params('INSERT INTO Item(item_id, item_name) VALUES($1, $2)', [itemid, name])
    end
  end

  def record_limit itemid, limit
    res = @conn.exec_params('UPDATE Item SET ge_limit=$1 WHERE item_id=$2', [limit, itemid])
    if res.cmd_tuples == 0
      puts "Couldn't insert limit for #{itemid}"
    end
  end

  def count_all
    return @conn.exec_params('SELECT COUNT(*) AS count FROM Item')[0]["count"]
  end
end
