package org.shiftlab.controllers.payload;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;

public enum Period {
    DAY {
        @Override
        public LocalDateTime getStartDate(Clock clock) {
            return LocalDateTime.now(clock).minusDays(1).with(LocalTime.MIN);
        }
    },
    MONTH {
        @Override
        public LocalDateTime getStartDate(Clock clock) {
            return LocalDateTime.now(clock).withDayOfMonth(1).with(LocalTime.MIN);
        }
    },
    QUARTER {
        @Override
        public LocalDateTime getStartDate(Clock clock) {
            int month = LocalDateTime.now(clock).getMonthValue();
            int startMonth = ((month - 1) / 3) * 3 + 1;
            return LocalDateTime.now(clock).withMonth(startMonth).withDayOfMonth(1).with(LocalTime.MIN);
        }

    },
    YEAR {
        @Override
        public LocalDateTime getStartDate(Clock clock) {
            return LocalDateTime.now(clock).withMonth(1).withDayOfMonth(1).with(LocalTime.MIN);
        }

    };

    public static Period fromString(String period) {
        try {
            return Period.valueOf(period.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException();
        }
    }
    public abstract LocalDateTime getStartDate(Clock clock);
}
