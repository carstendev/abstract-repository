package free

import model.User
import cats.free.Free


/**
  * Free monad encoding.
  */
sealed trait UserRepositoryAlg[T]
case class Find(id: Long) extends UserRepositoryAlg[Option[User]]
case class Update(u: User) extends UserRepositoryAlg[Unit]


object UserRepositorySyntax {
  type UserRepository[T] = Free[UserRepositoryAlg, T] // free monad binding is done on the heap

  def find(id: Long): UserRepository[Option[User]] = Free.liftF(Find(id))
  def update(u: User): UserRepository[Unit] = Free.liftF(Update(u))
}

