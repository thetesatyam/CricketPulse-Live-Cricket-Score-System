# CricketPulse Cricket Scoring Rules & Engine Specifications

CricketPulse implements the official Laws of Cricket (adapted for limited overs T20/ODI/T10):

## 1. Legal vs. Illegal Deliveries
- **Legal Ball**: Standard deliveries where the bowler delivers the ball without illegal foot faults or excessive height/width. Increments `legalBalls` count.
- **Wide Ball**: Extra run awarded to batting team (`wide_runs`). Does **not** count towards the bowler's 6 legal balls in an over. The batter is not charged with a ball faced.
- **No-Ball**: Penalty run awarded (`no_ball_runs`). Does **not** count towards the legal ball count. Any batter runs scored off a no-ball are credited to the batter, while the penalty is recorded as an extra. Batter is credited with a ball faced.
- **Byes & Leg-Byes**: Extra runs awarded to the team total without crediting the batter or charging the bowler's economy.

## 2. Strike Rotation Logic
- **Odd Runs (1, 3, 5)**: When an odd number of physical runs (batter runs, byes, or leg-byes) is completed, striker and non-striker swap ends.
- **Even Runs (0, 2, 4, 6)**: Batsmen return to their original ends; striker remains on strike.
- **End of Over**: When 6 legal deliveries are completed in an over:
  - Batsmen swap ends.
  - A new bowler must be designated (the same bowler cannot bowl consecutive overs).
- **Dismissals**: If a wicket falls:
  - When the striker is dismissed (bowled, caught, lbw, stumped, hit wicket), the incoming batter takes strike, unless an odd run occurred prior to a run-out.

## 3. Bowler vs. Fielding Credits
- **Credited to Bowler**: `BOWLED`, `CAUGHT`, `LBW`, `STUMPED`, `HIT_WICKET`. Wickets increment the bowler's tally.
- **Fielding Only**: `RUN_OUT` is **not** credited to the bowler's tally, but credited to the assisting fielder's stats.

## 4. Innings & Match Termination
- **All Out**: 10 wickets fallen in an innings.
- **Overs Exhausted**: Target overs (e.g. 20 overs = 120 legal balls) reached.
- **Target Achieved**: In the second innings, if the batting team reaches or exceeds the target (`totalRuns >= target`), the innings ends immediately with a victory by wickets.
- **Match Results**:
  - `Team A won by X runs`
  - `Team B won by Y wickets`
  - `Match tied` (scores equal after 2nd innings)
