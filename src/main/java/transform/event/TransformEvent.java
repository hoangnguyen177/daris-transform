package transform.event;

import arc.xml.XmlWriter;

public class TransformEvent extends arc.event.Event {

	public static final String EVENT_TYPE = "transform";

	public static class Filter implements arc.event.Filter {

		private String _id;

		public Filter(String id) {

			_id = id;
		}

		public boolean accept(arc.event.Event e) {

			if (!(e instanceof TransformEvent)) {
				return false;
			}
			if (!(e.type().equals(TransformEvent.EVENT_TYPE))) {
				return false;
			}
			TransformEvent te = (TransformEvent) e;
			if (_id != null) {
				return _id.equals(te.id());
			} else {
				return true;
			}
		}
	}

	public enum Action {
		CREATE, UPDATE, DESTROY
	}

	private Action _action;
	private String _id;

	public TransformEvent(String id, Action action) {

		super(EVENT_TYPE, true);
		_action = action;
		_id = id;
	}

	public TransformEvent(long tuid, Action action) {
		this(Long.toString(tuid), action);
	}

	public Action action() {

		return _action;
	}

	public String id() {

		return _id;
	}

	@Override
	public boolean equals(arc.event.Event e) {

		if (!super.equals(e)) {
			return false;
		}
		if (!(e instanceof TransformEvent)) {
			return false;
		}
		TransformEvent poe = (TransformEvent) e;
		return _action == poe.action() && _id.equals(poe.id());
	}

	@Override
	protected void saveState(XmlWriter w) throws Throwable {

		w.add("action", _action.toString());
		w.add("tuid", _id);
	}
}
