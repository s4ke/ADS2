package com.github.s4ke.algo2.exercise8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.s4ke.algo2.unionfind.UF;

/**
 * Created by Martin on 10.12.2015.
 */
public class MazeAlgo {

	private static final String UPPER = "^";

	private static final Direction N = Direction.N;
	private static final Direction E = Direction.E;
	private static final Direction S = Direction.S;
	private static final Direction W = Direction.W;

	private enum Direction {
		N,
		E,
		S,
		W
	}

	private static Set of(Direction... dir) {
		return new HashSet<>( Arrays.asList( dir ) );
	}

	private static Set[][] mazeWorks = {
			{of( W, S ), of( E ), of( W, E, S ), of( W, S )},
			{of( N, E, S ), of( W, S ), of( N ), of( N, S )},
			{of( N, E, S ), of( W, N, E, S ), of( W, E, S ), of( W, N, S )},
			{of( N, E ), of( W, N ), of( N, E ), of( W, N, E )}
	};

	private static Set[][] mazeBroken = {
			{of( W, S ), of( E ), of( W, E, S ), of( W, S )},
			{of( N, E, S ), of( W, S ), of( N ), of( N, S )},
			{of( N, E, S ), of( W, N, E, S ), of( W, E, S ), of( W, N )},
			{of( N, E ), of( W, N ), of( N ), of( E )}
	};

	public static void printMaze(Set[][] maze) {
		for ( int i = 0; i < maze.length; ++i ) {
			printUpper( maze[i] );
			printMiddle( maze[i] );
			printLower( maze[i] );
		}
	}

	public static void printUpper(Set<Direction>[] row) {
		for ( int i = 0; i < row.length; ++i ) {
			System.out.print( " _" );
			if ( row[i].contains( N ) ) {
				System.out.print( " " );
			}
			else {
				System.out.print( "_" );
			}
			System.out.print( "_" );
		}
		System.out.println();
		for ( int i = 0; i < row.length; ++i ) {
			System.out.print( "|" );
			System.out.print( "   " );
		}
		System.out.print( "|" );
		System.out.println();
	}

	public static void printMiddle(Set<Direction>[] row) {
		boolean lastEast = row[0].contains( W );
		if ( row[0].contains( W ) ) {
			System.out.print( " " );
		}
		else {
			System.out.print( "|" );
		}
		for ( int i = 0; i < row.length; ++i ) {
			if ( lastEast && !row[i].contains( W ) ) {
				throw new AssertionError();
			}
			System.out.print( "   " );
			if ( row[i].contains( E ) ) {
				System.out.print( " " );
				lastEast = true;
			}
			else {
				System.out.print( "|" );
				lastEast = false;
			}
		}
		System.out.println();
	}

	public static void printLower(Set<Direction>[] row) {
		for ( int i = 0; i < row.length; ++i ) {
			System.out.print( "|" );
			System.out.print( "   " );
		}
		System.out.print( "|" );

		System.out.println();
		for ( int i = 0; i < row.length; ++i ) {
			System.out.print( " " + UPPER );
			if ( row[i].contains( S ) ) {
				System.out.print( " " );
			}
			else {
				System.out.print( UPPER );
			}
			System.out.print( UPPER );
		}
		System.out.println();
	}

	public static UF findSolution(Set<Direction>[][] maze, int start, int end) {
		int totalSize = maze.length * maze[0].length;
		int rowLength = maze[0].length;
		UF uf = new UF( totalSize );
		int mergeCount = 0;
		outer:
		for ( int i = 0; i < maze.length; ++i ) {
			for ( int j = 0; j < maze[0].length; ++j ) {
				int curIndex = to( i, j, rowLength );
				Set<Direction> open = maze[i][j];
				{
					if ( j < maze[0].length - 1 ) {
						int other = to( i, j + 1, rowLength );
						mergeCount = maybeMerge( curIndex, other, uf, open, E, totalSize ) ?
								mergeCount + 1 :
								mergeCount;
					}
				}
				if ( uf.connected( start, end ) ) {
					break outer;
				}

				{
					if ( i < maze.length - 1 ) {
						int other = to( i + 1, j, rowLength );
						mergeCount = maybeMerge( curIndex, other, uf, open, S, totalSize ) ?
								mergeCount + 1 :
								mergeCount;
					}
				}
				if ( uf.connected( start, end ) ) {
					break outer;
				}
			}
		}
		System.out.println( "mergeCount: " + mergeCount );
		return uf;
	}

	/*public static List<Integer> startToFinish(UF uf, int start, int finish) {
		List<Integer> ret = new ArrayList<>();
		if ( !uf.connected( start, finish ) ) {
			throw new AssertionError();
		}
		int cur = finish;
		ret.add( 0, cur );
		System.out.println( "starting at: " + cur );
		while ( cur != start ) {
			while ( uf.from( cur ) != -1 ) {
				int tmp = uf.from( cur );
				if ( tmp != -1 ) {
					if ( ret.contains( tmp ) ) {
						break;
					}
					ret.add( 0, tmp );
					if ( ret.contains( tmp ) ) {
						//throw new AssertionError();
					}
					System.out.println( "going backwards to: " + tmp );
					cur = tmp;
				}
			}
			if ( cur != start ) {
				ret.remove( 0 );
			}
			while ( uf.to( cur ) != -1 ) {
				int tmp = uf.to( cur );
				if ( tmp != -1 ) {
					if ( ret.contains( tmp ) ) {
						break;
					}
					ret.add( 0, tmp );
					System.out.println( "going backwards to: " + tmp );
					cur = tmp;
				}
			}
		}
		return ret;
	}*/

	public static boolean exists(int other, int totalSize) {
		return other >= 0 && other <= (totalSize - 1);
	}

	public static boolean maybeMerge(
			int cur,
			int other,
			UF uf,
			Set<Direction> open,
			Direction direction,
			int totalSize) {
		if ( exists( other, totalSize ) ) {
			//only merge these two if they are not already
			//in the same tree, this way we produce a path
			//from the start to the finish node
			if ( !uf.connected( cur, other ) ) {
				if ( open.contains( direction ) ) {
					System.out.println( "merging " + other + " into " + cur );
					uf.union( cur, other );
					System.out.println( uf );
					return true;
				}
			}
		}
		return false;
	}

	public static int to(int i, int j, int rowLength) {
		return i * rowLength + j;
	}

	public static void main(String[] args) {
		go( 0, 15 );
	}

	public static void go(int start, int finish) {
		printMaze( mazeWorks );
		UF uf = findSolution( mazeWorks, start, finish );
		System.out.println( uf.connected( start, finish ) );
		if ( uf.connected( start, finish ) ) {
			for ( int i = 0; i < 16; ++i ) {
				System.out.print( String.valueOf( i ) + ":" + String.valueOf( uf.from( i ) ) + "; " );
			}
			System.out.println();
		}
	}

}
