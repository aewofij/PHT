package PHT;

import java.util.*;
import com.cycling74.max.*;

/** Represents a set of spatialized speakers, allowing for sounds to be distributed through the set. */
public class PHTSpeakerSet extends MaxObject {
	private Map<Integer, PHTSpeaker> _speakerSet;
	private Map<String, PHTSound> _liveSounds; // sounds that are currently living (active)

	private final PHTSpeakerFactory _speakerFactory;

	/** Main constructor for PHTSpeakerSet. */
	public PHTSpeakerSet() {
		_speakerSet = new HashMap<Integer, PHTSpeaker>();
		_liveSounds = new HashMap<String, PHTSound>();
		_speakerFactory = new PHTSpeakerFactory();

		// MaxObject stuff
		declareInlets(new int[]{ DataTypes.MESSAGE });
		declareOutlets(new int[]{ DataTypes.MESSAGE });
	}

	/* ------ Max methods ------------------------------------- */

	/** Adds a speaker to the set.
	 *	args: speaker <index> <x-pos> <y-pos> <z-pos> [<linked-speaker-idx1> <linked-speaker-idx2> ...]
 	 */
	public void speaker(Atom[] args) {
		if (args.length >= 4) {
			int[] linkedIndices = new int[args.length - 4];
			for (int i = 0; i < linkedIndices.length; i++) {
				linkedIndices[i] = args[i + 4].getInt();
			}

			_speakerFactory.addSpeaker(args[0].getInt(), 
										args[1].getFloat(),
										args[2].getFloat(), 
										args[3].getFloat(), 
										linkedIndices);
		}
	}

	/** Called after all speakers have been input to construct internal representations. */
	public void make_speakers() {
		_speakerSet = _speakerFactory.make();

		// DEBUG: Print speaker set.
		for (Integer i : _speakerSet.keySet()) {
			post(i + ": " + _speakerSet.get(i));
		}
	}

	/** Starts a new transect sound.
	 * args: transect_sound <id> <initial-speaker-index> [<lifespan>] */
	public void transect_sound(Atom[] args) {
		if (args.length == 3) {
			if (args[0].isString() && args[1].isInt() && args[2].isInt()) {
				PHTSpeaker initialSpeaker = get(args[1].getInt());

				if (initialSpeaker != null) {
					PHTSound newSound = new PHTTransectSound(args[0].getString(), initialSpeaker, args[2].getInt());
					_liveSounds.put(newSound.id(), newSound);
				} else {
					post("Invalid initial speaker index " + args[1].getInt() + " for transect sound " + args[0].getString() + ".");
				}
			}
		} else if (args.length == 2) {
			if (args[0].isString() && args[1].isInt()) {
				PHTSpeaker initialSpeaker = get(args[1].getInt());

				if (initialSpeaker != null) {
					PHTSound newSound = new PHTTransectSound(args[0].getString(), initialSpeaker);
					_liveSounds.put(newSound.id(), newSound);
				} else {
					post("Invalid initial speaker index " + args[1].getInt() + " for transect sound " + args[0].getString() + ".");
				}
			}
		}

		// DEBUG: Print sound set.
		post("--- SOUNDS ---");
		for (String s : _liveSounds.keySet()) {
			post(_liveSounds.get(s).id());
		}
	}

	/** Starts a new directed transect sound.
	 *  args: directed_transect_sound <id> <goal-x> <goal-y> <goal-z> <initial-speaker-index> <lifespan>
	 *  alternative: directed_transect_sound <id> <goal-speaker-index> <initial-speaker-index> <lifespan> */
	public void directed_transect_sound(Atom[] args) {
		if (args.length == 6) {
			if (args[0].isString()
				&& args[1].isFloat() && args[2].isFloat() && args[3].isFloat()
				&& args[4].isInt() && args[5].isInt()) {
				PHTSpeaker initialSpeaker = get(args[4].getInt());

				if (initialSpeaker != null) {
					PHTSound newSound = new PHTDirectedTransectSound(args[0].getString(), initialSpeaker, args[5].getInt(),
						new double[] {args[1].getFloat(), args[2].getFloat(), args[3].getFloat()});
					_liveSounds.put(newSound.id(), newSound);
				} else {
					post("Invalid initial speaker index " + args[4].getInt() + " for directed transect sound " + args[0].getString() + ".");
				}
			}
		} else if (args.length == 4) {
			if (args[0].isString()
				&& args[1].isInt()
				&& args[2].isInt() && args[3].isInt()) {
				PHTSpeaker initialSpeaker = get(args[2].getInt());
				PHTSpeaker goalSpeaker = get(args[1].getInt());

				if (initialSpeaker != null && goalSpeaker != null) {
					PHTSound newSound = new PHTDirectedTransectSound(args[0].getString(), initialSpeaker, args[3].getInt(), goalSpeaker.getPosition());
					_liveSounds.put(newSound.id(), newSound);
				} else {
					if (initialSpeaker == null) {
						post("Invalid initial speaker index " + args[2].getInt() + " for directed transect sound " + args[0].getString() + ".");
					}
					if (goalSpeaker == null) {
						post("Invalid goal speaker index " + args[1].getInt() + " for directed transect sound " + args[0].getString() + ".");
					}
				}
			}
		} else {
			post("Invalid number of args for directed_transect_sound.");
			return;
		}

		// DEBUG: Print sound set.
		post("--- SOUNDS ---");
		for (String s : _liveSounds.keySet()) {
			post(_liveSounds.get(s).id());
		}
	}

	/** Starts a new sweeping sound.
	 * args: sweep_sound <id> <start-x> <start-y> <start-z> <end-x> <end-y> <end-z> <travel-time> */
	public void sweep_sound(Atom[] args) {
		if (args.length != 8) {
			post("Invalid number of args for sweep_sound.");
			return;
		}

		PHTSweepSound newSound = new PHTSweepSound(args[0].getString(), 
													new double[] {args[1].getFloat(), args[2].getFloat(), args[3].getFloat()},
													new double[] {args[4].getFloat(), args[5].getFloat(), args[6].getFloat()},
													args[7].getInt(),
													this);

		_liveSounds.put(newSound.id(), newSound);

		// DEBUG: Print sound set.
		post("--- SOUNDS ---");
		for (String s : _liveSounds.keySet()) {
			post(_liveSounds.get(s).id());
		}
	}

	/** Immediately kills the sound specified by `id` if it is currently live.
	 *	@param 	id  	the String ID of the sound to kill */
	public void kill_sound(String id) {
		PHTSound s = _liveSounds.get(id);

		if (s != null) {
			killSound(s);
		}
	}

	/** Bang to update sound positions / age and output status. */
	public void bang() {
		Collection<PHTSound> allLiveSounds = _liveSounds.values();
		Collection<PHTSound> toKill = new LinkedList<PHTSound>();

		for (PHTSound s : allLiveSounds) {
			if (!s.act()) { // PHTTransectSound.act() will return false if sound will die.
				// We can't remove them in this loop, so add them to
				// 	the list and remove later.
				toKill.add(s);
			}
		}

		for (PHTSound s : toKill) {
			killSound(s);
		}

		status();
	}

	/** Outputs a series of Max message for each sound's status:
	 * outlet0:	<sound-id> <speaker1-index> <speaker1-gain> [<speaker2-index> <speaker2-gain> ...]
	 * Before output, a "begin" message is sent through outlet0. 
	 * When all have been output, a "done" message is sent through outlet0. */
	public void status() {
		outlet(0, "begin");

		Collection<PHTSound> allLiveSounds = _liveSounds.values();

		for (PHTSound s : allLiveSounds) {
			Map<PHTSpeaker, Double> speakerMap = s.speakerMap();
			Atom[] result = new Atom[speakerMap.size() * 2 + 1];
			int counter = 0;

			result[counter++] = Atom.newAtom(s.id());

			Set<PHTSpeaker> speakerKeys = speakerMap.keySet();
			for (PHTSpeaker speaker : speakerKeys) {
				result[counter++] = Atom.newAtom(speaker.getIndex());
				result[counter++] = Atom.newAtom(speakerMap.get(speaker));
			}

			outlet(0, result);
		}

		outlet(0, "done");
	}

	/** Clears speaker set and kills all live sounds. */
	public void clear() {
		_speakerFactory.clear();
		_liveSounds = new HashMap<String, PHTSound>();
	}

	// DEBUG: Change maximum spatialization distance.
	public void maximum_dist(float d) {
		PHTToolbox.kSpatializationDistanceMaximum = d;
	}

	/* ------ Internal methods ------------------------------------- */

	/* NOTE: These `add` methods are not really useful, since they require the user to add
	 		all speakers first, and then link them up. Use PHTSpeakerFactory instead (see
	 		speaker() and make_speaker() for this). */

	// // Adds one speaker to the set.
	// protected void add(PHTSpeaker newSpeaker) {
	// 	_speakerSet.put(newSpeaker.getIndex(), newSpeaker);
	// }

	// protected void addAll(PHTSpeaker... speakers) {
	// 	for (PHTSpeaker s : speakers) {
	// 		add(s);
	// 	}
	// }

	protected PHTSpeaker get(int index) {
		return _speakerSet.get(index);
	}

	// Calculates gain factor for each speaker in the set to represent a point sound at the provided `position`.
	protected Map<PHTSpeaker, Double> spatialize(double[] position) {
		// post("spatializing...");
		Map<PHTSpeaker, Double> result = new HashMap<PHTSpeaker, Double>();

		for (Integer i : _speakerSet.keySet()) {
			result.put(_speakerSet.get(i), distanceToGain(distance3D(position, _speakerSet.get(i).getPosition())));
			// post(result.get(i) + ": " + result.get(_speakerSet.get(i)));
		}

		return result;
	}

	protected void killSound(PHTSound s) {
		// DEBUG: Post dying sounds.
		post("killing: " + s.id());

		Atom[] toOutput = s.kill();
		if (toOutput != null && toOutput.length > 0) {
			outlet(0, toOutput);
		}

		_liveSounds.remove(s.id());
	}

	/* ------ Helpers ------------------------------------- */

	private double distance3D(double[] p0, double[] p1) {
		if (p0.length != 3 || p1.length != 3) {
			post("Invalid number of dimensions.");
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

	private double distanceToGain(double distance) {
		if (distance >= PHTToolbox.kSpatializationDistanceMaximum) {
			return 0.;
		}

		double gainDB = -80. * distance / PHTToolbox.kSpatializationDistanceMaximum;
	    double gainFactor = Math.pow(10., (0.05 * gainDB));

	    return gainFactor;
	}
}