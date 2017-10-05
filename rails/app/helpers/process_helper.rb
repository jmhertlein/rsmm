module ProcessHelper
  def hitsplat r, rs_type, process_name
    success = r["success"]
    img = "#{success ? "blue" : "red"}_hitsplat.png"
    link = "/process/error/#{rs_type}/#{process_name}/#{r["run_id"]}"
    if success
      return image_tag(img)
    else
      #return link_to image_tag(img), link
      return image_tag(img)
    end
  end
end
