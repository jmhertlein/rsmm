class ItemTracker
  def initialize conn
    @conn = conn
  end

  def name_for id
    res = @conn.exec_params('SELECT pretty_name FROM Item WHERE itemid=$1', [id])
    return res.ntuples == 0 ? nil : res[0]["pretty_name"]
  end

  def id_for item_name
    res = @conn.exec_params('SELECT itemid FROM Item WHERE pretty_name=$1', [item_name])
    return res.ntuples == 0 ? nil : res[0]["itemid"]
  end

  def record_id itemid, name
    puts "Updating #{itemid} => #{name}"
    res = @conn.exec_params('UPDATE Item SET pretty_name=$1 WHERE itemid=$2', [name, itemid])
    if res.cmd_tuples == 0
      puts "Couldn't update, inserting..."
      @conn.exec_params('INSERT INTO Item(itemid, pretty_name) VALUES($1, $2)', [itemid, name])
    end
  end

  def count_all
    return @conn.exec_params('SELECT COUNT(*) AS count FROM Item')[0]["count"]
  end
end
