module TradeConfig
  DB_INFO = {
    host: "claudius",
    port: 5432,
    dbname: "rsmm",
    user: "rsmm",
    sslmode: "require",
    sslcert: File.join(Dir.home, ".postgresql", "rsmm.crt"),
    sslkey: File.join(Dir.home, ".postgresql", "rsmm.key"),
    sslrootcert: File.join(Dir.home, ".postgresql", "root.crt")
  }

  MAIL_INFO = {
    sender_host: "trade.jmhertlein.net",
    recipients: "jmhertlein@gmail.com",
    mail_host: "trade.jmhertlein.net",
  }
end
