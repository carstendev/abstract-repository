package basic

import model.User

import scala.concurrent.{ExecutionContext, Future}

/**
  * This non abstract user repository is bound to the Future monad.
  * When testing, this forces us to implement code for waiting and the like.
  * This is a sign of the signatures not being abstract enough.
  */
trait UserRepository {
  def find(id: Long): Future[Option[User]]
  def update(user: User): Future[Unit]
}

class UserRepositoryImpl(implicit ec: ExecutionContext) extends UserRepository {
  override def find(id: Long): Future[Option[User]] =
    Future.successful(Some(User(1, "Carsten", 0))) // talk to the db and select user...

  override def update(user: User): Future[Unit] = {
    println(s"update for user $user")
    Future.successful(()) // talk to the db and update user
  }
}
