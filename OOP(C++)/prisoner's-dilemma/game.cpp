#include "game.h"
#include "strategy.h"


constexpr std::size_t ROWS = 8;

static int choices_to_idx(const std::vector<Choice> &choices) noexcept //choices to binary code
{
	return static_cast<int>(choices[0]) * 4 + static_cast<int>(choices[1]) * 2 + static_cast<int>(choices[2]) * 1;
}

std::vector<int> Matrix::get_payoffs(const std::vector<Choice> &choices) const noexcept
{
	return matrix_[choices_to_idx(choices)];
}

std::vector<int> &Matrix::operator [](std::size_t idx) noexcept
{
	assert (idx < ROWS && idx >= 0);
	return matrix_[idx];
}

Matrix::Matrix()
{
	matrix_ =
	{
		std::vector<int>{7, 7, 7},
		std::vector<int>{3, 3, 9},
		std::vector<int>{3, 9, 3},
		std::vector<int>{0, 5, 5},
		std::vector<int>{9, 3, 3},
		std::vector<int>{5, 0, 5},
		std::vector<int>{5, 5, 0},
		std::vector<int>{1, 1, 1},
	};
}

void Game::step()
{
	//ask for choices
	for (std::size_t i = 0; i < strategies_.size(); ++i)
	{
		strategies_[i]->make_choice();
		res_.choices_[i] = strategies_[i]->get_choice();
	}
	//get payoffs
	res_.payoffs_ = matrix_.get_payoffs(res_.choices_);
	//add to scores
	for (std::size_t i = 0; i < strategies_.size(); ++i)
	{
		res_.scores_[i] += res_.payoffs_[i];
	}
	//handle the result
	for (auto &strategy : strategies_)
	{
		strategy->handle_result(res_);
	}
}

Game::Game(const Matrix &matrix, std::vector<std::unique_ptr<Strategy>> strategies) : matrix_(matrix), strategies_(std::move(strategies)), res_()
{}

Result Game::get_result() const noexcept
{
	return res_;
}

Result::Result(int cols) :choices_(cols), payoffs_(cols), scores_(cols)
{}

bool operator ==(const Result &first, const Result &second) noexcept
{
	return (first.payoffs_ == second.payoffs_ && first.choices_ == second.choices_ && first.scores_ == second.scores_);
}
