Gem::Specification.new do |s|
  s.name        = "trade-config"
  s.version     = "0.0.1"
  s.platform    = Gem::Platform::RUBY
  s.authors     = ["Joshua Hertlein"]
  s.email       = ["jmhertlein@gmail.com"]
  s.summary     = "Default prod config for TJN"
  s.description = "Default mail and db config for TJN"

  s.required_rubygems_version = ">= 1.3.6"

  s.files        = Dir["{lib}/**/*.rb"]
  s.require_path = 'lib'
end
