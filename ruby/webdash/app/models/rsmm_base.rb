class RSMMBase < ActiveRecord::Base
  self.abstract_class = true
  establish_connection :rsmm_db
end
