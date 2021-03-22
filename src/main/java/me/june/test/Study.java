package me.june.test;

public class Study {
	private Status status = Status.DRAFT;
	private int limit;

	public enum Status {
		DRAFT,
		;
	}

	public Study(int limit) {
		if (limit > 0) {
			throw new IllegalArgumentException("limit 은 0보다 커야합니다.");
		}
		this.limit = limit;
	}

	public Status getStatus() {
		return status;
	}

	public int getLimit() {
		return limit;
	}
}
