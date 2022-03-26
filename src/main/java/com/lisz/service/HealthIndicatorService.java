package com.lisz.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

@Service
public class HealthIndicatorService implements HealthIndicator {
	@Setter
	@Getter
	private Boolean up = true;

	@Override
	public Health health() {
		if (up) {
			return new Health.Builder().up().build();
		}
		return new Health.Builder().down().build();
	}
}
