class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception
  around_filter :cache_other_db_connections

  private

  def cache_other_db_connections
    RSMMBase.connection.cache { yield }
  end

  def not_found
    raise ActionController::RoutingError.new('Not Found')
  end
end
