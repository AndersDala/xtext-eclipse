package org.eclipse.xtext.example.homeautomation.tests

import com.google.inject.Inject
import org.eclipse.xtext.example.homeautomation.ruleEngine.Model
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper

class ParserTest extends AbstractTest {

	@Inject extension ParseHelper<Model>
	@Inject extension ValidationTestHelper

	override protected test(CharSequence document) {
		document.parse.assertNoErrors
	}

}