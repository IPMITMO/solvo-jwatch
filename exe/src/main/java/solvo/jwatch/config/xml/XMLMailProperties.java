/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 29, 2015
 */

package solvo.jwatch.config.xml;

/**
 *
 * @author user_akrikheli
 */
public class XMLMailProperties
{
    private String protocol;
    private String host;
    private Integer port;
    private Boolean auth;
    private String user;
    private String password;
    private String fromAddr;

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public Boolean getAuth()
    {
        return auth;
    }

    public void setAuth(Boolean auth)
    {
        this.auth = auth;
    }

    public String getFromAddr()
    {
        return fromAddr;
    }

    public void setFromAddr(String fromAddr)
    {
        this.fromAddr = fromAddr;
    }
}
