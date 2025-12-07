-- Before running drop any existing views
DROP VIEW IF EXISTS q0;
DROP VIEW IF EXISTS q1i;
DROP VIEW IF EXISTS q1ii;
DROP VIEW IF EXISTS q1iii;
DROP VIEW IF EXISTS q1iv;
DROP VIEW IF EXISTS q2i;
DROP VIEW IF EXISTS q2ii;
DROP VIEW IF EXISTS q2iii;
DROP VIEW IF EXISTS q3i;
DROP VIEW IF EXISTS q3ii;
DROP VIEW IF EXISTS q3iii;
DROP VIEW IF EXISTS q4i;
DROP VIEW IF EXISTS q4ii;
DROP VIEW IF EXISTS q4iii;
DROP VIEW IF EXISTS q4iv;
DROP VIEW IF EXISTS q4v;

-- Question 0
CREATE VIEW q0(era)
AS
    SELECT MAX(era)
    FROM pitching
;

-- Question 1i
CREATE VIEW q1i(namefirst, namelast, birthyear)
AS
    SELECT namefirst, namelast, birthyear
    FROM people
    WHERE weight > 300
;

-- Question 1ii
CREATE VIEW q1ii(namefirst, namelast, birthyear)
AS
    SELECT namefirst, namelast, birthyear
    FROM people

    WHERE namefirst LIKE '% %' --see if there's an empty space
    ORDER BY namefirst, namelast
;

-- Question 1iii
CREATE VIEW q1iii(birthyear, avgheight, count)
AS
    SELECT birthyear, AVG(height), COUNT(*) --count:number of people
    FROM people
    GROUP BY birthyear
    ORDER BY birthyear
;

-- Question 1iv
CREATE VIEW q1iv(birthyear, avgheight, count)
AS
    SELECT birthyear, AVG(height), COUNT(*)
    FROM people
    GROUP BY birthyear
    HAVING  AVG (height) > 70
    ORDER BY birthyear
;

-- Question 2i
CREATE VIEW q2i(namefirst, namelast, playerid, yearid)
AS
    SELECT p.namefirst, p.namelast, p.playerid, h.yearid
    FROM people p
    JOIN halloffame h ON p.playerid = h.playerid
    WHERE h.inducted = 'Y'
    ORDER BY h.yearid DESC, p.playerid;
;

-- Question 2ii
CREATE VIEW q2ii(namefirst, namelast, playerid, schoolid, yearid)
AS
    SELECT q.namefirst, q.namelast, q.playerid, c.schoolid, q.yearid
    FROM q2i q
    INNER JOIN collegeplaying c ON q.playerid = c.playerid
    INNER JOIN schools s ON c.schoolid = s.schoolid
    WHERE s.schoolState = 'CA'
    ORDER BY q.yearid DESC, c.schoolid, q.playerid;

;

-- Question 2iii
CREATE VIEW q2iii(playerid, namefirst, namelast, schoolid)
AS
    SELECT q.playerid, q.namefirst, q.namelast, c.schoolid
    FROM q2i q
    LEFT JOIN collegeplaying c ON q.playerid = c.playerid
    ORDER BY q.playerid DESC, c.schoolid;
;

-- Question 3i
CREATE VIEW q3i(playerid, namefirst, namelast, yearid, slg)
AS
    SELECT p.playerid, p.namefirst, p.namelast, b.yearid,
         (b.H + b.H2B + 2*b.H3B + 3*b.HR + 0.0) / (b.AB + 0.0) AS slg
    FROM people p
    INNER JOIN batting b ON p.playerid = b.playerid
    WHERE b.AB>50

    ORDER BY slg DESC, b.yearid, p.playerid
    LIMIT 10;
;

-- Question 3ii
CREATE VIEW q3ii(playerid, namefirst, namelast, lslg)
AS
    SELECT p.playerid, p.namefirst, p.namelast, (SUM(b.H) + SUM(b.H2B) + 2 * SUM(b.H3B) + 3 * SUM(b.HR) + 0.0) / (SUM(b.AB) + 0.0) AS lslg
    FROM people p
    INNER JOIN batting b ON p.playerid = b.playerid
    GROUP BY p.playerid, p.namefirst, p.namelast
    HAVING SUM(b.AB) > 50
    ORDER BY lslg DESC, p.playerid
    LIMIT 10;
;

-- Question 3iii

CREATE VIEW q3iii(namefirst, namelast, lslg)
AS
WITH PlayerSlugging AS (
    SELECT playerid,
           (SUM(H) + SUM(H2B) + 2*SUM(H3B) + 3*SUM(HR) + 0.0) / (SUM(AB) + 0.0) AS lslg
    FROM batting
    GROUP BY playerid
    HAVING SUM(AB) > 50
),

MaysSlugging AS (
    SELECT (SUM(H) + SUM(H2B) + 2*SUM(H3B) + 3*SUM(HR) + 0.0) / (SUM(AB) + 0.0) AS mays_lslg
    FROM batting
    WHERE playerid = 'mayswi01'
)

SELECT p.namefirst, p.namelast, ROUND(ps.lslg, 4) AS lslg
FROM people p
JOIN PlayerSlugging ps ON p.playerid = ps.playerid
CROSS JOIN MaysSlugging ms
WHERE ps.lslg > ms.mays_lslg
ORDER BY ps.lslg DESC
;

-- Question 4i
CREATE VIEW q4i(yearid, min, max, avg)
AS
    SELECT yearid, MIN(salary), MAX(salary), AVG(salary)
    FROM salaries
    GROUP BY yearid
    ORDER BY yearid ASC;
;

-- Question 4ii
CREATE VIEW q4ii(binid, low, high, count)
AS
WITH SalaryStats AS (
    SELECT MIN(salary) AS min_salary, MAX(salary) AS max_salary
    FROM salaries
    WHERE yearid = 2016
),

BinRanges AS (
    SELECT binid,
           min_salary + (binid * (max_salary - min_salary) / 10) AS low,
           CASE
               WHEN binid = 9 THEN max_salary + 1  -- Ensure bin 9 includes all salaries up to and including the max.
               ELSE min_salary + ((binid + 1) * (max_salary - min_salary) / 10)
           END AS high
    FROM binids, SalaryStats
),

SalaryCounts AS (
    SELECT binid, COUNT(*) AS count
    FROM BinRanges br
    LEFT JOIN salaries s ON s.yearid = 2016
        AND s.salary >= br.low
        AND (s.salary < br.high OR br.binid = 9 AND s.salary = br.high)
    GROUP BY binid
)

SELECT br.binid, br.low, br.high, COALESCE(sc.count, 0) AS count
FROM BinRanges br
LEFT JOIN SalaryCounts sc ON br.binid = sc.binid
ORDER BY br.binid;
;

-- Question 4iii
CREATE VIEW q4iii(yearid, mindiff, maxdiff, avgdiff)
AS
WITH SalaryStats AS (
    SELECT yearid,
           MIN(salary) AS min_salary,
           MAX(salary) AS max_salary,
           AVG(salary) AS avg_salary
    FROM salaries
    GROUP BY yearid
),

SalaryDiffs AS (
    SELECT
        s1.yearid,
        s1.min_salary - s2.min_salary AS mindiff,
        s1.max_salary - s2.max_salary AS maxdiff,
        ROUND(s1.avg_salary - s2.avg_salary, 4) AS avgdiff
    FROM SalaryStats s1
    JOIN SalaryStats s2 ON s1.yearid = s2.yearid + 1
)

SELECT yearid, mindiff, maxdiff, avgdiff
FROM SalaryDiffs
ORDER BY yearid;

;

-- Question 4iv
CREATE VIEW q4iv(playerid, namefirst, namelast, salary, yearid)
AS --Select the playerid, namefirst, namelast, salary, and yearid for players in 2000 and 2001 who had the maximum salary
WITH MaxSalary AS (
    SELECT yearid, MAX(salary) AS max_salary
    FROM salaries
    WHERE yearid IN (2000, 2001) --restrict the results to the years 2000 and 2001
    GROUP BY yearid
)
SELECT s.playerid, p.namefirst, p.namelast, s.salary, s.yearid
FROM salaries s
--join the people table to retrieve the namefirst and namelast for each player.
INNER JOIN people p ON s.playerid = p.playerid
INNER JOIN MaxSalary ms ON s.yearid = ms.yearid AND s.salary = ms.max_salary
WHERE s.yearid IN (2000, 2001)
ORDER BY s.yearid, s.playerid;

;
-- Question 4v
CREATE VIEW q4v(team, diffAvg) AS

WITH AllStarSalaries AS (
    SELECT a.teamid, s.salary
    FROM allstarfull a --links each All-Star to their salary based on playerid and yearid
    JOIN salaries s ON a.playerid = s.playerid AND a.yearid = s.yearid
    WHERE a.yearid = 2016
),
TeamSalaryStats AS (
    SELECT teamid,
           MAX(salary) AS max_salary,
           MIN(salary) AS min_salary
    FROM AllStarSalaries
    GROUP BY teamid
)
SELECT teamid AS team,
       (max_salary - min_salary)
       AS diffAvg --the difference between the highest paid All-Star and the lowest paid All-Star for each team
FROM TeamSalaryStats;
;

