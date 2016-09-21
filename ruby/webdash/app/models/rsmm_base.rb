class RSMMBase < ActiveRecord::Base
  self.abstract_class = true
  establish_connection configurations['rsmm_db'][Rails.env]
end
