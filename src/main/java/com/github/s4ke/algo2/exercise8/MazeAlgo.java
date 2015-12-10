package com.github.s4ke.algo2.exercise8;

import java.util.Arrays;
import java.util.HashSet;
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

	private static Set[][] maze = {
			{of( W, S ), of( E ), of( W, E, S ), of( W, S )},
			{of( N, E, S ), of( W, S ), of( N ), of( N, S )},
			{of( N, E, S ), of( W, N, E, S ), of( W, E, S ), of( W, N, S )},
			{of( N, E ), of( W, N ), of( N, E ), of( W, N, E )}
	};

	/*private static Set[][] maze = {
			{of( W, S ), of( E ), of( W, E, S ), of( W, S )},
			{of( N, E, S ), of( W, S ), of( N ), of( N, S )},
			{of( N, E, S ), of( W, N, E, S ), of( W, E, S ), of( W, N )},
			{of( N, E ), of( W, N ), of( N ), of( E )}
	};*/

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

	public static boolean hasSolution(Set<Direction>[][] maze, int start, int end) {
		int totalSize = maze.length * maze[0].length;
		int rowLength = maze[0].length;
		UF uf = new UF( totalSize );
		for ( int i = 0; i < maze.length; ++i ) {
			for ( int j = 0; j < maze[0].length; ++j ) {
				int curIndex = to( i, j, rowLength );
				Set<Direction> open = maze[i][j];
				if ( open.contains( E ) && exists( i, j + 1, rowLength, totalSize ) ) {
					System.out.println( "merging " + to( i, j + 1, rowLength ) + " into " + curIndex );
					uf.union( curIndex, to( i, j + 1, rowLength ) );
				}
				if ( open.contains( S ) && exists( i + 1, j, rowLength, totalSize ) ) {
					System.out.println( "merging " + to( i + 1, j, rowLength ) + " into " + curIndex );
					uf.union( curIndex, to( i + 1, j, rowLength ) );
				}
				System.out.println( uf );
				if ( uf.connected( start, end ) ) {
					return true;
				}
			}
		}
		System.out.println( uf );
		return uf.connected( start, end );
	}

	public static boolean exists(int i, int j, int rowLength, int totalSize) {
		return to( i, j, rowLength ) >= 0 && to( i, j, rowLength ) <= (totalSize - 1);
	}


	public static int to(int i, int j, int rowLength) {
		return i * rowLength + j;
	}

	public static void main(String[] args) {
		printMaze( maze );
		System.out.println( hasSolution( maze, 0, 15 ) );
	}

}
