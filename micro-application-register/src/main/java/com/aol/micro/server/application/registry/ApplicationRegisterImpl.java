package com.aol.micro.server.application.registry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aol.cyclops.invokedynamic.ExceptionSoftener;
import com.aol.micro.server.servers.ApplicationRegister;
import com.aol.micro.server.servers.model.ServerData;

import lombok.Getter;

@Component
public class ApplicationRegisterImpl implements ApplicationRegister {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Getter
	private volatile Application application;

	@Value("${configuration.hostname:#null}")
	private String customHostname;
	
	public ApplicationRegisterImpl() {
		
	}

	@Override
	public void register(ServerData[] data) {
		String hostname;
		try {
			hostname = customHostname == null ? InetAddress.getLocalHost().getHostName() : customHostname;
			application = new Application(Stream
					.of(data)
					.map(next -> new RegisterEntry(next.getPort(), hostname, next.getModule().getContext(), next
							.getModule().getContext(), null)).collect(Collectors.toList()));
			logger.info("Registered application {} ", application);
		} catch (UnknownHostException e) {
			ExceptionSoftener.throwSoftenedException(e);
		}
	}
}
