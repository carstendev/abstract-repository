package basic

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class ClientRankManager(userRepository: UserRepository) {

  def updateRank(userId: Long, rank: Int)(implicit ec: ExecutionContext): Future[Either[String, Unit]] =
    userRepository.find(userId).flatMap {
      case None =>
        Future.successful(Left("user not found"))

      case Some(user) =>
        userRepository.update(user.copy(rank = rank)).map(_ => Right(()))
    }

}

object Main extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  // Always bound to future
  val result: Future[Either[String, Unit]] =
    new ClientRankManager(new UserRepositoryImpl).updateRank(1, 10)

  println(Await.result(result, Duration.Inf))
}