Gem::Specification.new do |s|
  s.name        = "gescrape"
  s.version     = "0.0.1"
  s.platform    = Gem::Platform::RUBY
  s.authors     = ["Joshua Hertlein"]
  s.email       = ["jmhertlein@gmail.com"]
  s.homepage    = "https://jmhertlein.net"
  s.summary     = "GE-scraping utils"
  s.description = "Utilities for scraping info from the GE API"

  s.required_rubygems_version = ">= 1.3.6"

  s.files        = Dir["{lib}/**/*.rb", "bin/*", "LICENSE", "*.md"]
  s.require_path = 'lib'

  s.bindir = 'bin'
  s.executables = ["px_mon.rb", "download_items.rb", "scrape_limits.rb", "test_trade_email.rb"]
end
