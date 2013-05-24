load FullI
SimpleCalcExpectedUtility(FullI)
FullI2 = FullI;
FullI2.RandomFactors = NormalizeCPDFactors(ObserveEvidence(FullI.RandomFactors, [3 2]));
SimpleCalcExpectedUtility(FullI2)