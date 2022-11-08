package highseas.utilities;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIdGen implements IdGen {

	public static AtomicIdGen createDefaultIdGen() {
		return new AtomicIdGen(1, 1);
	}

	public static IdGen createIdGen(int startingValue, int incrementor) {
		return new AtomicIdGen(startingValue, incrementor);
	}

	public static IdGen createIdGenWithStartingValue(int startingValue) {
		return new AtomicIdGen(startingValue, 1);
	}

	private final AtomicInteger currentValue;

	private final int incrementor;

	private AtomicIdGen(int startingValue, int incrementor) {
		currentValue = new AtomicInteger(startingValue);
		this.incrementor = incrementor;
	}

	@Override
	public Integer getNextVal() {
		return currentValue.getAndAdd(incrementor);
	}

}
