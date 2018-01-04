/**
 * Copyright (C) 2015-2017 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.stream.scaladsl

import akka.stream._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.stream.testkit.scaladsl.TestSink
import akka.stream.testkit.{ StreamSpec, Utils }

class FlowDetacherSpec extends StreamSpec {

  implicit val materializer = ActorMaterializer()

  "A Detacher" should {

    "pass through all elements" in Utils.assertAllStagesStopped {
      Source(1 to 100)
        .detach
        .runWith(Sink.seq)
        .futureValue should ===(1 to 100)
    }

    "pass through failure" in Utils.assertAllStagesStopped {
      val ex = new Exception("buh")
      val result = Source(1 to 100)
        .map(x ⇒ if (x == 50) throw ex else x)
        .detach
        .runWith(Sink.seq)
      intercept[Exception] {
        Await.result(result, 2.seconds)
      } should ===(ex)

    }

    "emit the last element when completed without demand" in Utils.assertAllStagesStopped {
      Source.single(42)
        .detach
        .runWith(TestSink.probe)
        .ensureSubscription()
        .expectNoMsg(500.millis)
        .requestNext() should ===(42)
    }

  }

}
