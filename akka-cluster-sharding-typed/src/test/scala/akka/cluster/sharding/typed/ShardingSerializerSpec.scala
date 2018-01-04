/**
 * Copyright (C) 2009-2017 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.cluster.sharding.typed

import akka.actor.typed.TypedAkkaSpecWithShutdown
import akka.actor.typed.internal.adapter.ActorSystemAdapter
import akka.cluster.sharding.typed.internal.ShardingSerializer
import akka.serialization.SerializationExtension
import akka.testkit.typed.TestKit

class ShardingSerializerSpec extends TestKit with TypedAkkaSpecWithShutdown {

  "The typed ShardingSerializer" should {

    val serialization = SerializationExtension(ActorSystemAdapter.toUntyped(system))

    def checkSerialization(obj: AnyRef): Unit = {
      serialization.findSerializerFor(obj) match {
        case serializer: ShardingSerializer ⇒
          val blob = serializer.toBinary(obj)
          val ref = serializer.fromBinary(blob, serializer.manifest(obj))
          ref should ===(obj)
        case s ⇒
          throw new IllegalStateException(s"Wrong serializer ${s.getClass} for ${obj.getClass}")
      }
    }

    "must serialize and deserialize ShardingEnvelope" in {
      checkSerialization(ShardingEnvelope("abc", 42))
    }

    "must serialize and deserialize StartEntity" in {
      checkSerialization(StartEntity("abc"))
    }
  }
}
