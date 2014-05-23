package PHT;

import java.util.*;

/** Represents a speaker (audio channel) in 3D space, with sets of links to other speakers. */
public class PHTSpeaker {
	// Integer index of speaker.
	private final int _index;
	// Position of speaker in 3D.
	private final double[] _position;
	// Can hold multiple "layers" of links, using a string identifier for each group.
	private Map<String, List<PHTSpeakerLink>> _linksMap;
	private final java.util.Random _rand;

	/** Constructs a PHTSpeaker at a given position.
	 *
	 *	@param 	index 	integer index of this speaker (usually, the output channel)
	 *	@param 	x 		spatial x coordinate
	 *	@param 	y 		spatial y coordinate
	 *	@param 	z 		spatial z coordinate
	 */
	public PHTSpeaker(int index, double x, double y, double z) {
		_index = index;
		_position = new double[]{ x, y, z };
		_linksMap = new HashMap<String, List<PHTSpeakerLink>>();

		_rand = new java.util.Random();
	}

	/** Returns the spatial position of this speaker, in an array of { x, y, z } coordinates. */
	public double[] getPosition() {
		return _position;
	}

	/** Returns the integer index of this speaker. */
	public int getIndex() {
		return _index;
	}

	/** Links this speaker to another speaker on the specified link group.
	 *
	 *	@param 	speaker 	the speaker to which this speaker will be linked.
	 *	@param 	linkGroup	the name of the link group for the new link.
	 */
	public void linkTo(PHTSpeaker speaker, String linkGroup) {
		addLink(new PHTSpeakerLink(this, speaker), linkGroup);
	}

	/** Links this speaker to another speaker on the default link group (specifically, the "--default" group).
	 *
	 *	@param 	speaker 	the speaker to which this speaker will be linked.
	 */
	public void linkTo(PHTSpeaker speaker) {
		addLink(new PHTSpeakerLink(this, speaker), "--default");
	}

	/** Returns a random linked speaker from the specified link group.
	 *
	 *	@param 	linkGroup 	the name of the link group to pull from
	 *	@returns 			the random linked speaker, or null if no linked speakers in that group.
	 */
	public PHTSpeaker randomLinked(String linkGroup) {
		List<PHTSpeakerLink> linked = _linksMap.get(linkGroup);
		if (linked != null && linked.size() > 0) {
			return linked.get(_rand.nextInt(linked.size(
				))).destination;
		} 

		return null;
	}

	protected Map<String, List<PHTSpeakerLink>> linksMap() {
		return _linksMap;
	}

	protected void addLink(PHTSpeakerLink link, String linkGroup) {
		if (!_linksMap.containsKey(linkGroup)) {
			_linksMap.put(linkGroup, new ArrayList<PHTSpeakerLink>());
		}
		_linksMap.get(linkGroup).add(link);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

	    if (!(o instanceof PHTSpeaker)) {
	    	return false;
	    }

	    PHTSpeaker other = (PHTSpeaker) o;

	    return  (this.getPosition()[0] == other.getPosition()[0]) &&
	    		(this.getPosition()[1] == other.getPosition()[1]) &&
	    		(this.getPosition()[2] == other.getPosition()[2]) &&
	    		(this.getIndex() == other.getIndex());
	}

	@Override
	public int hashCode() {
		return _index; // indices should not overlap
	}

	@Override
	public String toString() {
		String result = String.format("%d - position: { %.2f, %.2f, %.2f }; links: { ", _index, _position[0], _position[1], _position[2]);

		for (String linkKey : _linksMap.keySet()) {
			result += linkKey + ":[ ";

			for (PHTSpeakerLink aLink : _linksMap.get(linkKey)) {
				result += aLink.destination.getIndex() + " ";
			}

			result += "] ";
		}

		result += " }";
		return result;
	}
}	