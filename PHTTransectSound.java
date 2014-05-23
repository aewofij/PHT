package PHT;

import java.util.*;
import com.cycling74.max.*;

/* Represents a sound that moves from a single speaker to a single other speaker. */
public class PHTTransectSound implements PHTSound {
	private final String _id;
	private final long _lifespan;
	private long _age;
	private long _birthTime; // in ms

	private PHTSpeaker _src; // momentary source speaker for travel from speaker to speaker
	private PHTSpeaker _dst; // momentary destination speaker for travel from speaker to speaker
	private long _startTime; // in ms
	private long _travelTime; // time for the current travel in ms
	private double _progress; // progress in [0., 1.] of current travel

	private Map<PHTSpeaker, Double> _speakerMap; // map of PHTSpeaker to gain factor

	private java.util.Random _random;

	/** Constructor for immortal transect sound.
	 *
	 *	@param	id 					the String ID of this sound
	 *	@param 	initialPosition		the initial speaker for this sound
	 */
	public PHTTransectSound(String id, PHTSpeaker initialPosition) {
		this(id, initialPosition, Long.MAX_VALUE);
	}

	/** Constructs a PHTTransectSound with a lifespan.
	 *
	 *	@param	id 					the String ID of this sound
	 *	@param 	initialPosition		the initial speaker for this sound
	 *	@param 	lifespan			the duration of this sound, in milliseconds
	 */
	public PHTTransectSound(String id, PHTSpeaker initialPosition, long lifespan) {
		_id = id;
		_age = 0;
		_lifespan = lifespan;

		_src = initialPosition;
		_dst = initialPosition;
		_progress = 1.;
		_startTime = System.currentTimeMillis();
		_birthTime = System.currentTimeMillis();

		_speakerMap = new HashMap<PHTSpeaker, Double>();

		_random = new java.util.Random();
	}

	/** Chooses next speaker for sound to travel to. 
	 * 
	 * 	@param 	from		the speaker from which the sound is traveling
	 *	@param	linkLayer	the ID for the link layer from which to draw the link to follow
	 */
	protected PHTSpeaker chooseNextDestination(PHTSpeaker from, String linkLayer) {
		return from.randomLinked(linkLayer);
	}

	/* ------ PHTSound ------------------------------------- */

	/** Updates this sound's speaker map, relying on computer's clock. 
	 *	@returns 	true if sound is still alive, else false.
	 */
	public boolean act() {
		// Update age.
		_age = System.currentTimeMillis() - _birthTime;

		// Check for death.
		if (willDie()) {
			return false;
		}

		// Choose new destination if necessary.
		if (_progress >= 1.) {
			_src = _dst;
			_dst = chooseNextDestination(_src, "--default");
			_progress = 0.;
			_travelTime = (long)(distance3D(_src.getPosition(), _dst.getPosition()) * PHTToolbox.kDistanceToTimeRatio);

			_startTime = System.currentTimeMillis();

			// DEBUG: Posting which sound is traveling where.
			// post(id() + ": moving from " + _src.getIndex() + " to " + _dst.getIndex() + " over " + _travelTime + " ms.");
		}

		// Update progression.
		_progress = ((double)(System.currentTimeMillis() - _startTime) / (double)_travelTime);

		// DEBUG: Outputting progress / age for a single sound.
		// outlet(0, "progress", new Atom[] {Atom.newAtom(_progress)});
		// outlet(0, "age", new Atom[] {Atom.newAtom(_age)});

		updateSpeakerMap();

		return true;
	}

	/** Returns a map of speakers to gain factor.
	 *  @returns 	a Map of PHTSpeaker to the corresponding gain factor (between 0. and 1.).
	 */
	public Map<PHTSpeaker, Double> speakerMap() {
		return _speakerMap;
	}

	/** Returns this sound's String ID. */
	public String id() {
		return _id;
	}

	/** Called on kill. */
	public Atom[] kill() {
		return new Atom[] { Atom.newAtom(_id), Atom.newAtom("killed") };
	}

	/* ------ Helpers ------------------------------------- */

	private boolean willDie() {
		return _age > _lifespan;
	}

	private void updateSpeakerMap() {
		_speakerMap.clear();
		_speakerMap.put(_src, 1. - _progress);
		_speakerMap.put(_dst, _progress);
	}

	private double distance3D(double[] p0, double[] p1) {
		if (p0.length != 3 || p1.length != 3) {
			return -1;
		}

		return distance3D(p0[0], p0[1], p0[2], p1[0], p1[1], p1[2]);
	}

	private double distance3D(double x0, double y0, double z0, double x1, double y1, double z1) {
		double deltaX = x1 - x0;
		double deltaY = y1 - y0;
		double deltaZ = z1 - z0;

		return (double)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}
}