package com.github.avthart.smtp.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.james.metrics.api.NoopMetricFactory;
import org.apache.james.protocols.api.Protocol;
import org.apache.james.protocols.api.handler.ProtocolHandler;
import org.apache.james.protocols.netty.NettyServer;
import org.apache.james.protocols.smtp.SMTPConfigurationImpl;
import org.apache.james.protocols.smtp.SMTPProtocol;
import org.apache.james.protocols.smtp.SMTPProtocolHandlerChain;
import org.jboss.netty.util.HashedWheelTimer;
import org.springframework.lang.NonNull;

import java.net.InetSocketAddress;
import java.util.Collection;

@Slf4j
public class SmtpServer {

    private final SmtpServerProperties properties;

    private final Collection<ProtocolHandler> handlers;

    private NettyServer server;

    public SmtpServer(@NonNull SmtpServerProperties properties, Collection<ProtocolHandler> handlers) {
        this.properties = properties;
        this.handlers = handlers;
    }

    public void start() throws Exception {
        SMTPConfigurationImpl smtpConfiguration = new SMTPConfigurationImpl();
        smtpConfiguration.setSoftwareName(properties.getSoftwareName());

        SMTPProtocolHandlerChain chain = new SMTPProtocolHandlerChain(new NoopMetricFactory());
        chain.addAll(0, handlers);
        chain.wireExtensibleHandlers();

        Protocol protocol = new SMTPProtocol(chain, smtpConfiguration);

        server = new NettyServer.Factory(new HashedWheelTimer())
                .protocol(protocol)
                .build();
        server.setListenAddresses(new InetSocketAddress(properties.getPort()));
        server.setTimeout(properties.getTimeout());
        server.bind();
        log.info("SMTP Server started on port: {}", properties.getPort());
    }


    public void stop() {
        server.unbind();
        log.info("SMTP Server stopped on port: {}", properties.getPort());
    }
}