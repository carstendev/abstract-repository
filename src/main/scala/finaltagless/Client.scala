package finaltagless

import cats.Monad
import cats.implicits._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
  * As our repository is not bound to a result monad, our client also doesn't have to be.
  */
class UserClient[F[_] : Monad](repository: UserRepository[F]) {

  def setRank(userId: Long, rank: Int): F[Either[String, Unit]] = {
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
    new UserClient(new FutureUserRepository).setRank(1, 10)

  // blocking
  val blockingResult: Try[Either[String, Unit]] =
    new UserClient(new BlockingUserRepository).setRank(1, 10)

  val fResult =
    Await.result(futureResult, Duration.Inf)

  println(fResult)
  println(blockingResult)

}