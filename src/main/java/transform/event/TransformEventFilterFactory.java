package transform.event;

public class TransformEventFilterFactory implements arc.event.FilterFactory {

	public static TransformEventFilterFactory INSTANCE = new TransformEventFilterFactory();

	private TransformEventFilterFactory() {

	}

	@Override
	public arc.event.Filter create(String id, boolean descend) {

		return new TransformEvent.Filter(id);
	}

}
