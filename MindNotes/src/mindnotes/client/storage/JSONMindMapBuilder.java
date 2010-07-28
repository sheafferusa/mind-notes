package mindnotes.client.storage;

import mindnotes.shared.model.MindMapBuilder;
import mindnotes.shared.model.NodeLocation;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSONMindMapBuilder implements MindMapBuilder {

	private class JSONNodeBuilder implements NodeBuilder {

		JSONObject _node;
		JSONArray _children;

		public JSONNodeBuilder() {
			_node = new JSONObject();
			_children = new JSONArray();
			_node.put("children", _children);
		}

		public JSONNodeBuilder(JSONObject node) {
			_node = node;
			_children = _node.get("children").isArray();
		}

		@Override
		public void copyTo(NodeBuilder nb) {
			nb.setExpanded(_node.get("expanded").isBoolean().booleanValue());

			try {
				nb.setText(_node.get("text").isString().stringValue());
			} catch (NullPointerException npe) {
				nb.setText(null);
			}

			try {
				nb.setNodeLocation(NodeLocation.valueOf(_node.get("location")
						.isString().stringValue()));
			} catch (NullPointerException npe) {
				nb.setNodeLocation(null);
			}

			for (int i = 0; i < _children.size(); i++) {
				new JSONNodeBuilder(_children.get(i).isObject()).copyTo(nb
						.createNode());
			}
		}

		@Override
		public NodeBuilder createNode() {
			JSONNodeBuilder jnb = new JSONNodeBuilder();
			_children.set(_children.size(), jnb.getJSONObject());
			return jnb;
		}

		@Override
		public void setExpanded(boolean expanded) {
			_node.put("expanded", JSONBoolean.getInstance(expanded));
		}

		@Override
		public void setNodeLocation(NodeLocation location) {
			if (location != null)
				_node.put("location", new JSONString(location.toString()));
		}

		@Override
		public void setText(String text) {
			if (text != null)
				_node.put("text", new JSONString(text));
		}

		public JSONValue getJSONObject() {
			return _node;
		}

	}

	private JSONObject _map;

	public JSONMindMapBuilder() {
		_map = new JSONObject();
	}

	public JSONMindMapBuilder(JSONObject map) {
		_map = map;
	}

	@Override
	public void copyTo(MindMapBuilder mmb) {
		try {
			mmb.setTitle(_map.get("title").isString().stringValue());
		} catch (NullPointerException npe) {
			mmb.setTitle(null);
		}
		new JSONNodeBuilder(_map.get("rootNode").isObject()).copyTo(mmb
				.createRootNode());
	}

	@Override
	public NodeBuilder createRootNode() {
		JSONNodeBuilder jnb = new JSONNodeBuilder();
		_map.put("rootNode", jnb.getJSONObject());
		return jnb;
	}

	@Override
	public void setTitle(String title) {
		if (title != null) {
			_map.put("title", new JSONString(title));
		}
	}

	public JSONObject getJSONObject() {
		return _map;
	}

	public String getJSON() {
		return _map.toString();
	}

}
