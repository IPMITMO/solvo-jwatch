/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 29, 2015
 */

package solvo.jwatch.config;

/**
 *
 * @author user_akrikheli
 */
public class MailProperties
{
     /**
     * Default values
     */
    public static String DEFAULT_PROTOCOL     = "smtp";

    private Protocol protocol;
    private String host;
    private Integer port;
    private Boolean auth;
    private String user;
    private String password;
    private String fromAddr;

    public enum Protocol
    {
        SMTP
        {
            @Override
            public String toString()
            {
                return "smtp";
            }
        },
        SMTPS
        {
            @Override
            public String toString()
            {
                return "smtps";
            }
        }
    }

    protected MailProperties()
    {
        
    }

    public Protocol getProtocol()
    {
        return protocol;
    }

    public String getHost()
    {
        return host;
    }

    public Integer getPort()
    {
        return port;
    }

    public Boolean getAuth()
    {
        return auth;
    }

    public String getUser()
    {
        return user;
    }

    public String getPassword()
    {
        return password;
    }

    public String getFromAddr()
    {
        return fromAddr;
    }

    public static Builder newBuilder()
    {
        return new MailProperties().new Builder();
    }

    public class Builder
    {
        private Builder()
        {

        }

        public MailProperties build()
        {
            return MailProperties.this;
        }

        public Builder setProtocol(String protocol)
        {
            if (protocol == null)
            {
                MailProperties.this.protocol = Protocol.SMTP;
            }
            else
            {
                if (protocol.equals("smtps"))
                {
                    MailProperties.this.protocol = Protocol.SMTPS;
                }
                else
                {
                    MailProperties.this.protocol = Protocol.SMTP;
                }
            }
            return this;
        }

        public Builder setHost(String host)
        {
            if (host != null)
            {
                MailProperties.this.host = host;
            }
            return this;
        }

        public Builder setPort(Integer port)
        {
            if (port != null)
            {
                MailProperties.this.port = port;
            }
            return this;
        }

        public Builder setAuth(Boolean auth)
        {
            if (auth != null)
            {
                MailProperties.this.auth = auth;
            }
            return this;
        }

        public Builder setUser(String user)
        {
            if (user != null)
            {
                MailProperties.this.user = user;
            }
            return this;
        }

        public Builder setPassword(String password)
        {
            if (password != null)
            {
                MailProperties.this.password = password;
            }
            return this;
        }

        public Builder setFromAddr(String fromAddr)
        {
            if (fromAddr != null)
            {
                MailProperties.this.fromAddr = fromAddr;
            }
            return this;
        }
    }

}
