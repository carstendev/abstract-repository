package taglessfinal

import model.User

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

/**
  * Using the tagless final encoding, we abstract over the result monad,
  * leaving us free to use different implementations.
  */
trait UserRepository[F[_]] {
  def find(id: Long): F[Option[User]]
  def update(user: User): F[Unit]
}

class FutureUserRepository(implicit ex: ExecutionContext) extends UserRepository[Future] {
  override def find(id: Long): Future[Option[User]] =
    Future.successful(Some(User(1, "Carsten", 0)))

  override def update(user: User): Future[Unit] = {
    println(s"future update for user $user")
    Future.successful(())
  }
}

class BlockingUserRepository extends UserRepository[Try] {
  override def find(id: Long): Try[Option[User]] =
    Success(Some(User(1, "Carsten", 0)))

  override def update(user: User): Try[Unit] = {
    println(s"try update for user $user")
    Try(())
  }
}


