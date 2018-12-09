package taglessfinal

import cats.Monad
import cats.implicits._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
  * As our repository is not bound to a result monad, our client also doesn't have to be.
  */
class ClientRankManager[F[_] : Monad](repository: UserRepository[F]) {

  def updateRank(userId: Long, rank: Int): F[Either[String, Unit]] = {
    repository.find(userId).flatMap {
      case None =>
        implicitly[Monad[F]].pure(Left("user not found"))

      case Some(user) =>
        val updated = user.copy(rank = rank)
        repository.update(updated).map(_ => Right(()))
    }
  }
}


object Main extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  // non blocking
  val futureResult: Future[Either[String, Unit]] =
    new ClientRankManager(new FutureUserRepository).updateRank(1, 10)

  // blocking
  val blockingResult: Try[Either[String, Unit]] =
    new ClientRankManager(new BlockingUserRepository).updateRank(1, 10)

  println(Await.result(futureResult, Duration.Inf))
  println(blockingResult)

}