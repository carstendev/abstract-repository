package basic

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class Client(userRepository: UserRepository) {

  def updateRank(userId: Long, rank: Int)(implicit ec: ExecutionContext): Future[Unit] =
    userRepository.find(userId).flatMap { user =>
      userRepository.update(user.copy(rank = rank))
    }

}

object Main extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  // Always bound to future
  val result: Future[Unit] =
    new Client(new UserRepositoryImpl).updateRank(1, 10)

  println(Await.result(result, Duration.Inf))
}