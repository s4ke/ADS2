package com.github.s4ke.algo2.exercise9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Martin on 20.12.2015.
 */
public class Change {

	private static class InfiniteList<T> extends ArrayList<T> {

		public static InfiniteList INSTANCE = new InfiniteList();

		@Override
		public int size() {
			return Integer.MAX_VALUE;
		}

	}

	private static class HackMap<K, V> extends HashMap<K, V> {

		@Override
		public V get(Object k) {
			Double val = (Double) k;
			if ( val.doubleValue() < 0 ) {
				return (V) InfiniteList.INSTANCE;
			}
			else {
				return super.get( k );
			}
		}

	}

	private static Double[] MONEY = {0.01D, 2D, 4D};
	private static List<Double> MONEY_LIST = Arrays.asList( MONEY );

	public static List<Double> makeChange(List<Double> coinsIn, double n) {
		List<Double> coins = new ArrayList<>( coinsIn );
		Collections.sort( coins );
		Double smallest = coins.get( 0 );
		if ( n < smallest ) {
			throw new IllegalArgumentException( "cannot make change for something smaller than: " + smallest );
		}
		Map<Double, List<Double>> map = new HackMap<>();
		map.put( 0.0D, Collections.emptyList() );
		//Hack, needed for Java to work with Doubles
		//could have fixed this differently, but welp this works
		//at least for 0.01D as the smallest, we don't really care...
		double roundFactor = Math.pow( 10, Math.signum( smallest ) * Math.ceil( Math.abs( Math.log10( smallest ) ) ) );
		for ( double A = smallest; A <= n; A = Math.round( (A + smallest) * roundFactor ) / roundFactor ) {
			List<Double> min = InfiniteList.INSTANCE;
			for ( int c = 0; c < coins.size(); ++c ) {
				double coin = coins.get( c );
				//coin fits into the thing
				if ( coin <= A ) {
					List<Double> minusCoin = map.get( Math.round( (A - coin) * roundFactor ) / roundFactor );
					if ( minusCoin == null ) {
						minusCoin = new ArrayList<>();
					}
					if ( minusCoin.size() < min.size() ) {
						List<Double> copy = new ArrayList<>( minusCoin );
						copy.add( coin );
						min = copy;
					}
				}
			}
			map.put( A, min );
			System.out.println( "minimum for: " + A + " is " + min );
		}
		return map.get( n );
	}

	public static void main(String[] args) {
		System.out.println( makeChange( MONEY_LIST, 93D ) );
	}

}
