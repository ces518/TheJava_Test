package me.june.test.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Member owner;

	private Status status = Status.DRAFT;
	private int limit;
	private String name;

	public enum Status {
		DRAFT,
		;
	}

	public Study(int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("limit 은 0보다 커야합니다.");
		}
		this.limit = limit;
	}

	public Study(int limit, String name) {
		this.limit = limit;
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public int getLimit() {
		return limit;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Study{" +
			"status=" + status +
			", limit=" + limit +
			", name='" + name + '\'' +
			'}';
	}
}
