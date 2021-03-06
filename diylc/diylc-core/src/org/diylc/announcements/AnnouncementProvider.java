package org.diylc.announcements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.diylc.appframework.miscutils.ConfigurationManager;
import org.diylc.plugins.cloud.model.IServiceAPI;

import com.diyfever.httpproxy.PhpFlatProxy;
import com.diyfever.httpproxy.ProxyFactory;

public class AnnouncementProvider {

  private String serviceUrl = "http://www.diy-fever.com/diylc/api/v1/announcements.html";
  private String LAST_READ_KEY = "announcement.lastReadDate";

  private IAnnouncementService service;

  private Date lastDate;
  private List<Announcement> announcements;

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  public AnnouncementProvider() {
    String lastDateStr = ConfigurationManager.getInstance().readString(LAST_READ_KEY, null);
    try {
      this.lastDate = lastDateStr == null ? null : dateFormat.parse(lastDateStr);
    } catch (ParseException e) {
    }
    serviceUrl =
        ConfigurationManager.getInstance().readString(IServiceAPI.URL_KEY, "http://www.diy-fever.com/diylc/api/v1");
    ProxyFactory factory = new ProxyFactory(new PhpFlatProxy());
    service = factory.createProxy(IAnnouncementService.class, serviceUrl);
  }

  public String getCurrentAnnouncements(boolean forceLast) throws ParseException {
    announcements = service.getAnnouncements();
    boolean hasUnread = false;
    StringBuilder sb = new StringBuilder("<html>");
    for (int i = 0; i < announcements.size(); i++) {
      Date date = dateFormat.parse(announcements.get(i).getDate());
      if (lastDate == null || lastDate.before(date) || (forceLast && i == announcements.size() - 1)) {
        sb.append("<font size='4'><b>").append(announcements.get(i).getTitle()).append("</b> on ")
            .append(announcements.get(i).getDate()).append("</font>").append("<p>")
            .append(announcements.get(i).getText()).append("</p>");
        hasUnread = true;
      }
    }
    if (!hasUnread)
      return "";    
    sb.append("</html>");
    return sb.toString();
  }

  public void dismissed() {
    Date date = new Date();
    ConfigurationManager.getInstance().writeValue(LAST_READ_KEY, dateFormat.format(date));
  }
}
