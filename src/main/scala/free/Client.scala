package free

import cats.free.Free
import cats.implicits._
import cats.~>
import model.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try
import scala.util.Success


/**
  * As our repository is not bound to a result monad, our client also doesn't have to be.
  */
class UserClient {

  import free.UserRepositorySyntax._

  def setRank(userId: Long, rank: Int): UserRepository[Either[String, Unit]] = {
    find(userId).flatMap {
      case None =>
        Free.pure(Left("user not found"))

      case Some(user) =>
        val updated = user.copy(rank = rank)
        update(updated).map(_ => Right(()))
    }
  }

}


object Main extends App {

  val futureInterpreter = new (UserRepositoryAlg ~> Future) {
    override def apply[A](fa: UserRepositoryAlg[A]): Future[A] = fa match {
      case Find(id) =>
        /* go and talk to a database */
        Future.successful(Some(User(1, "Carsten", 1)))
      case Update(user) =>
        println(s"future update for user $user")
        /* as above */
        Future.successful(())
    }
  }

  val nonBlockingInterpreter = new (UserRepositoryAlg ~> Try) {
    override def apply[A](fa: UserRepositoryAlg[A]): Try[A] = fa match {
      case Find(id) =>
        /* go and talk to a database */
        Success(Some(User(1, "Carsten", 1)))
      case Update(user) =>
        println(s"try update for user $user")
        /* as above */
        Success(())
    }
  }

  // non blocking
  val futureResult: Future[Either[String, Unit]] =
    new UserClient().setRank(1, 10).foldMap(futureInterpreter)

  // blockingResult
  val blockingResult: Try[Either[String, Unit]] =
    new UserClient().setRank(1, 10).foldMap(nonBlockingInterpreter)

  println(Await.result(futureResult, Duration.Inf))
  println(blockingResult)
}