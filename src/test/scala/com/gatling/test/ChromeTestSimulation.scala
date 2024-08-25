package com.gatling.test

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ChromeTestSimulation extends Simulation {

	val httpProtocol = http
		.baseUrl("https://www.abercrombie.com/")
		.inferHtmlResources(    BlackList(
			""".*\.css""",
			""".*\.js""",
			""".*\.png""",
			""".*\.jpg""",
			""".*\.gif""",
			""".*\.svg"""
		),
			// Whitelist: only allow requests matching these patterns
			WhiteList(
				""".*\.html""",
				""".*\.php"""
			)
		)
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")

	val scn = scenario("User Journey on Abercrombie Website")
		.exec(
			http("Homepage")
				.get("https://www.abercrombie.com/shop/wd")
				.check(status.is(200))
		)
		.pause(1) // Pause for 3 seconds to simulate user thinking time
		.exec(
			http("PLP PAGE")
				.get("https://www.abercrombie.com/shop/wd/mens-new-arrivals")
				.check(status.is(200))
		)
		.pause(1)
		.exec(
			http("PDP Page")
				.get("https://www.abercrombie.com/shop/wd/p/cropped-twill-zip-shirt-jacket-56348829?categoryId=12835&faceout=model&seq=03 ")
				.check(status.is(200))
		)
		.pause(1)
		.exec(
			http("Add Product to Cart")
				.post("https://www.abercrombie.com/shop/OrderItemDisplayView?storeId=11203&catalogId=10901&langId=-1&orderStatus=P")
				.formParam("productId", "56348829")
				.formParam("quantity", "1")
				.check(status.is(200))
		)
		.pause(1)
		.exec(
			http("Proceed to Checkout")
				.get("https://www.abercrombie.com/shop/OrderCheckoutDisplayView?storeId=11203&catalogId=10901&langId=-1&doInventory=N&InvRepuCheckMode=checkout&URL=OrderCheckoutDisplayView&removeSoldOutIems=true&errpage=OrderItemDisplayView&checkoutMode=webCheckout")
				.check(status.is(200))
		)

	// Define the load simulation: ramp up to 200 users over 60 seconds
	setUp(
		scn.inject(
			rampUsers(150) during (60.seconds)
		)
	).protocols(httpProtocol)

}