package highseas.utilities;

import java.util.function.Supplier;

@FunctionalInterface
public interface IdGen extends Supplier<Integer> {

	@Override
	default Integer get() {
		return getNextVal();
	}

	Integer getNextVal();

}