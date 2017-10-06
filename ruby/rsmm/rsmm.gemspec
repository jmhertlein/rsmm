Gem::Specification.new do |s|
  s.name        = "rsmm"
  s.version     = "2"
  s.platform    = Gem::Platform::RUBY
  s.authors     = ["Joshua Hertlein"]
  s.email       = ["jmhertlein@gmail.com"]
  s.homepage    = "https://josh.cafe"
  s.summary     = "Market Making on the GE"
  s.description = "Data-management and ETL processes for market-making on the GE"

  s.required_rubygems_version = ">= 1.3.6"

  s.files        = Dir["{lib}/**/*.rb", "bin/*", "LICENSE", "*.md"]
  s.require_path = 'lib'

  s.bindir = 'bin'
  s.executables = ["pxmon.rb", "itemdb.rb", "limitmon.rb", "test_trade_email.rb", "ge-svc.rb", "test-gesvc-client.rb"]

  s.add_runtime_dependency 'nokogiri'
  s.add_runtime_dependency 'pg'
  s.add_runtime_dependency 'colorize'

end
