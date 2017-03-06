module TradeConfig
  CFG = {
    prod: {
      db: {
        host: "claudius",
        port: 5432,
        dbname: "rsmm",
        user: "rsmm",
        sslmode: "require",
        sslcert: File.join(Dir.home, ".postgresql", "rsmm.crt"),
        sslkey: File.join(Dir.home, ".postgresql", "rsmm.key"),
        sslrootcert: File.join(Dir.home, ".postgresql", "root.crt")
      }, 
      mail: {
        sender_host: "trade.jmhertlein.net",
        recipients: "jmhertlein@gmail.com",
        mail_host: "trade.jmhertlein.net",
      }
    },

    dev: {
      db: {
        host: "localhost",
        port: 5432,
        dbname: "rsmm",
        user: "rsmm",
        sslmode: "allow",
      }, 
      mail: {
        sender_host: "trade.jmhertlein.net",
        recipients: "jmhertlein@gmail.com",
        mail_host: "trade.jmhertlein.net",
      }
    }
  }

  def self.for mode, svc, key=nil
    if key.nil?
      return CFG[mode][svc]
    else
      return CFG[mode][svc][key]
    end
  end
end
