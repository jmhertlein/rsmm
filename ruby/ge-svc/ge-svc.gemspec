Gem::Specification.new do |s|
  s.name        = "ge-svc"
  s.version     = "0.0.1"
  s.platform    = Gem::Platform::RUBY
  s.authors     = ["Joshua Hertlein"]
  s.email       = ["jmhertlein@gmail.com"]
  s.homepage    = "https://jmhertlein.net"
  s.summary     = "GE-scraping daemon"
  s.description = "A daemon to proxy GE API requests among many clients"

  s.required_rubygems_version = ">= 1.3.6"

  s.files        = Dir["{lib}/**/*.rb", "bin/*", "LICENSE", "*.md"]
  s.require_path = 'lib'

  s.bindir = 'bin'
  s.executables = ["ge-svc.rb"]
end
