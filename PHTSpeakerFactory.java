package PHT;

import java.util.*;

/** This class is used to create a set of linked PHTSpeakers. */
public class PHTSpeakerFactory {
	private class PHTRawSpeaker {
		public final int index;
		public final double[] position;
		// Link set does not hold links, but the indices of connected speakers.
		public Set<Integer> linkSet;

		public PHTRawSpeaker(int index, double[] position, int[] linkedSpeakerIndices) {
			this.index = index;
			this.position = position;

			this.linkSet = new HashSet<Integer>();
			for (int idx : linkedSpeakerIndices) {
				this.linkSet.add(idx);
			}
		}
	}

	private Map<Integer, PHTRawSpeaker> _rawSpeakerSet;

	/** Main constructor for PHTSpeakerFactory. */
	public PHTSpeakerFactory() {
		_rawSpeakerSet = new HashMap<Integer, PHTRawSpeaker>();
	}

	/** Adds speaker to factory, without constructing actual PHTSpeaker representation.
	 *	@param 	index 					the index of the speaker being added
	 *	@param 	x						the spatial x coordinate of the speaker being added
	 *	@param 	y						the spatial y coordinate of the speaker being added
	 *	@param 	z						the spatial z coordinate of the speaker being added
	 *	@param 	linkedSpeakerIndices	the indices of the speakers to which this speaker is linked.
	 */
	public PHTSpeakerFactory addSpeaker(int index, double x, double y, double z, int[] linkedSpeakerIndices) {
		_rawSpeakerSet.put(index, new PHTRawSpeaker(index, new double[]{x, y, z,}, linkedSpeakerIndices));
		return this;
	}

	/** Constructs PHTSpeaker representations 
	 *	@returns 	a mapping of speaker index to PHTSpeaker representation. */ 
	public Map<Integer, PHTSpeaker> make() throws IndexOutOfBoundsException {
		Map<Integer, PHTSpeaker> result = new HashMap<Integer, PHTSpeaker>();

		Set<Integer> keySet = _rawSpeakerSet.keySet();

		// Construct PHTSpeaker representations.
		for (Integer i : keySet) {
			PHTSpeaker cooked = new PHTSpeaker(_rawSpeakerSet.get(i).index, 
												_rawSpeakerSet.get(i).position[0], 
												_rawSpeakerSet.get(i).position[1], 
												_rawSpeakerSet.get(i).position[2]);
			result.put(i, cooked);
		}

		// Make links.
		for (Integer i : keySet) {
			for (Integer linkIdx : _rawSpeakerSet.get(i).linkSet) {
				if (result.get(linkIdx) != null) {
					result.get(i).linkTo(result.get(linkIdx));
				} else {
					throw new IndexOutOfBoundsException("Tried to link speaker " + i + " to nonexistant speaker " + linkIdx + ".");
				}
			}
		}

		return result;
	}

	/** Resets factory. */
	public PHTSpeakerFactory clear() {
		_rawSpeakerSet.clear();
		return this;
	}
}