Gem::Specification.new do |s|
  s.name        = 'ge-api'
  s.version     = '0.0.1'
  s.date        = '2016-07-11'
  s.summary     = "A gem to bring the Runescape GE API to Ruby"
  s.description = "A gem to bring the Runescape GE API to Ruby."
  s.authors     = ["Joshua Hertlein"]
  s.email       = 'jmhertlein@gmail.com'
  s.files       = ["lib/"]
  s.homepage    =
    'https://jmhertlein.net'
  s.license       = 'GPL-3.0'
  s.platform    = Gem::Platform::RUBY
  s.required_rubygems_version = ">= 1.3.6"

  # If you need to check in files that aren't .rb files, add them here
  s.files        = Dir["{lib}/**/*.rb"]
  s.require_path = 'lib'
end
