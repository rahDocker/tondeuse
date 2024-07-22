package com.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mowltnow.tondeuse.entity.Position;
import com.mowltnow.tondeuse.entity.Tondeuse;
import com.mowltnow.tondeuse.service.TondeuseService;

@ExtendWith(MockitoExtension.class)
public class TondeuseServiceTest {
	
	@InjectMocks 
	TondeuseService tondeuseService;
	
	
	
	@Test
	void test() throws Exception {
	
		Position positionInitial = new Position(1,2,'N');
		
		Position positionFinal = new Position();
		Position positionFinalExpected = new Position(1,3,'N');
		
		Tondeuse tondeuse = new Tondeuse("GAGAGAGAA",positionInitial);
		positionFinal = tondeuseService.getFinalPosition(tondeuse,5,5);
		
		

		assertEquals(positionFinalExpected, positionFinal);
		
		
	}

}
