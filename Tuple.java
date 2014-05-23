package PHT;

public class Tuple<T, U> {
	public final T hd;
	public final U tl;

	public Tuple(T hd, U tl) {
		this.hd = hd;
		this.tl = tl;
	}
}