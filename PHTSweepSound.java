package PHT;

import java.util.*;
import com.cycling74.max.*;

/** Represents a sound which "sweeps" from one point to another in the sound field. */
public class PHTSweepSound extends MaxObject implements PHTSound {
	private final String _id;
	private Map<PHTSpeaker, Double> _speakerMap;
	private final PHTSpeakerSet _speakerSet;

	private final double[] _startPoint;
	private final double[] _endPoint;
	private final double[] _travelVector;
	private final long _startTime;
	private final long _travelTime;

	private double[] _currentPosition;

	/** Constructs a PHTSweepSound. 
	 *
	 * 	@param 	id 			the String ID of this sound
	 * 	@param 	startPoint 	the starting point of this sound's sweep, in an array of { x, y, z } spatial coordinates
	 * 	@param 	endPoint 	the end point of this sound's sweep, in an array of { x, y, z } spatial coordinates
	 * 	@param 	travelTime 	the duration of this sound's travel from startPoint to endPoint, in milliseconds
	 * 	@param 	speakerSet 	the PHTSpeakerSet this sound will be spatialized into
	 */
	public PHTSweepSound(String id, double[] startPoint, double[] endPoint, int travelTime, PHTSpeakerSet speakerSet) {
		_id = id;

		_startPoint = startPoint;
		_endPoint = endPoint;
		_currentPosition = _startPoint;
		_travelVector = new double[] { _endPoint[0] - _startPoint[0], 
										_endPoint[1] - _startPoint[1], 
										_endPoint[2] - _startPoint[2] };
		

		_startTime = System.currentTimeMillis();
		_travelTime = travelTime;

		_speakerMap = new HashMap<PHTSpeaker, Double>();
		_speakerSet = speakerSet;
	}

	/* ------ PHTSound ------------------------------------- */

	/** Updates this sound's speaker map, relying on computer's clock. 
	 *	@returns 	true if sound is still alive, else false.
	 */
	public boolean act() {
		// PHTSpeakerSet.PHTConsole.post("PHTSweepSound: acting");

		double progress = ((double)(System.currentTimeMillis() - _startTime) / (double)_travelTime);

		// Check for death.
		if (progress >= 1.) {
			return false;
		}

		// Update progression.
		_currentPosition = new double[] { _startPoint[0] + _travelVector[0] * progress, 
											_startPoint[1] + _travelVector[1] * progress, 
											_startPoint[2] + _travelVector[2] * progress };

		// DEBUG: Output current position of a single sound.
		// outlet(0, "pos", new Atom[] {  Atom.newAtom(_currentPosition[0]), 
		// 									Atom.newAtom(_currentPosition[1]), 
		// 									Atom.newAtom(_currentPosition[2]) });

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

	private void updateSpeakerMap() {
		_speakerMap = _speakerSet.spatialize(_currentPosition);
	}
}